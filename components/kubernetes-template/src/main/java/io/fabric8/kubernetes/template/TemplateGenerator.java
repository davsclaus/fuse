/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.kubernetes.template;

import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.utils.Files;
import io.fabric8.utils.Objects;
import io.fabric8.utils.Strings;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.fabric8.kubernetes.api.KubernetesHelper.validateKubernetesId;

/**
 */
public class TemplateGenerator {
    public static final String DEFAULT_TEMPLATE = "io/fabric8/templates/default.mvel";
    public static final String DOCKER_DATA_IMAGE = "docker_image";
    public static final String DEFAULT_NAMESPACE = "default";

    private final GenerateTemplateDTO config;
    private final List<ClassLoader> classLoaders;

    public TemplateGenerator(GenerateTemplateDTO config) {
        this(config, createDefaultClassLoaders());
    }

    public TemplateGenerator(GenerateTemplateDTO config, List<ClassLoader> classLoaders) {
        this.config = config;
        this.classLoaders = classLoaders;
    }

    private static List<ClassLoader> createDefaultClassLoaders() {
        List<ClassLoader> classLoaders = new ArrayList<>();
        classLoaders.add(Thread.currentThread().getContextClassLoader());
        classLoaders.add(TemplateGenerator.class.getClassLoader());
        return classLoaders;
    }


    public void generate(File kubernetesJson) throws IllegalArgumentException {
        String template = config.getTemplate();
        String dockerImage = config.getDockerImage();
        if (Strings.isNullOrBlank(template)) {
            throw new IllegalArgumentException("No fabric8.template specified so cannot generate the Kubernetes JSON file!");
        } else {
            InputStream in = loadTemplate(template);
            if (in == null) {
                throw new IllegalArgumentException("Could not find template: " + template + " on the ClassPath when trying to generate the Kubernetes JSON!");
            }
            ParserContext parserContext = new ParserContext();
            Map<String, Object> variables = new HashMap<>();
            variables.putAll(config.getTemplateVariables());
            if (Strings.isNotBlank(dockerImage)) {
                addIfNotDefined(variables, DOCKER_DATA_IMAGE, dockerImage);
            }
            Objects.notNull(variables.get(DOCKER_DATA_IMAGE), "no docker.image property specified!");
            String name = config.getName();
            addIfNotDefined(variables, "name", name);
            String namespace = config.getNamespace();
            if (namespace == null) {
                namespace = DEFAULT_NAMESPACE;
            }
            addIfNotDefined(variables, "namespace", namespace);
            addIfNotDefined(variables, "containerName", config.getContainerName());
            Map<String, String> labels = config.getLabels();
            variables.put("labels", labels);
            variables.put("ports", config.getPorts());
            variables.put("environmentVariables", config.getEnvironmentVariables());
            variables.put("imagePullPolicy", config.getImagePullPolicy());

            // replication controllers
            variables.put("replicaCount", config.getReplicaCount());
            String replicationControllerName =  validateKubernetesId(config.getReplicationControllerName(), "replicationControllerName");
            variables.put("replicationControllerName", replicationControllerName);

            // service
            String serviceName = config.getServiceName();
            if (Strings.isNotBlank(serviceName)) {
                serviceName = validateKubernetesId(serviceName, "serviceName");
            }
            if (Strings.notEmpty(serviceName)) {
                if (Objects.equal(serviceName, replicationControllerName)) {
                    throw new IllegalArgumentException("replicationControllerName and serviceName are the same! (" + serviceName + ")");
                }
            }
            variables.put("serviceName", serviceName);
            variables.put("servicePort", config.getServicePort());
            variables.put("serviceContainerPort", config.getServiceContainerPort());


            try {
                CompiledTemplate compiledTemplate = TemplateCompiler.compileTemplate(in, parserContext);
                String answer = TemplateRuntime.execute(compiledTemplate, parserContext, variables).toString();
                String generated = answer;
                Files.writeToFile(kubernetesJson, generated, Charset.defaultCharset());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to generate Kubernetes JSON from template " + template + ". " + e, e);
            }
        }
    }

    protected InputStream loadTemplate(String template) {
        for (ClassLoader classLoader : classLoaders) {
            InputStream answer = classLoader.getResourceAsStream(template);
            if (answer != null) {
                return answer;
            }
        }
        return null;
    }

    protected static <T> void addIfNotDefined(Map<String, T> map, String key, T value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }
}
