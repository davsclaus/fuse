## fabric8:migrate

The maven `fabric8:migrate` goal helps migrate your project from 2.x of the `fabric8-maven-plugin` to version 3.x.

This goal takes the generated Kubernetes manifest in `target/classes/kubernetes.yml` and generates the source files in `src/main/fabric8` that will be detected by the 3.x plugin.

### Prerequisites

**Note** before using this goal please make sure you have checked in all your changes to source control as it will overwrite and modify your source code! Namely the `pom.xml` file and files in `src/main/fabric8/*.yml`
 
## Example

To migrate your `fabric8-maven-plugin` 2.x project to 3.x try:
 
    mvn fabric8:migrate

If you are feeling brave you can migrate and try deploy the project in one line:

    mvn fabric8:migrate io.fabric8:fabric8-maven-plugin:3.1.11:resource io.fabric8:fabric8-maven-plugin:3.1.11:deploy
    
Then your new app should be built and applied.
    
You can compare the difference between the old and new manifests by comparing these files
    
* `target/classes/kubernetes.yml` is the 2.x generated manifest    
* `target/classes/META-INF/fabric8/kubernetes.yml` is the 3.x generated manifest
    
Note that if you use any OpenShift Template parameters in your manifest then the YAML files will be considerably different; there will be no OpenShift Template and the parameters will be replaced by a `ConfigMap` and the property expression environment variables of the form `${FOO}`
   
```yaml
        - env:
          - name: "FOO"
            valueFrom:
              configMapKeyRef:
                key: "foo"
                name: "${project.artifactId}"
```   

will be replaced with a `configMapKeyRef` instead:

```   
      - env:
        - name: "FOO"
          valueFrom:
            configMapKeyRef:
              key: "foo"
              name: "${project.artifactId}"
```   