/**
 *  Copyright 2005-2015 Red Hat, Inc.
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
package io.fabric8.forge.camel.commands.project.model;

public class CamelEndpointDetails {

    private String fileName;
    private String lineNumber;
    private String endpointComponentName;
    private String endpointInstance;
    private String endpointUri;
    private boolean consumerOnly;
    private boolean producerOnly;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getEndpointComponentName() {
        return endpointComponentName;
    }

    public void setEndpointComponentName(String endpointComponentName) {
        this.endpointComponentName = endpointComponentName;
    }

    public String getEndpointInstance() {
        return endpointInstance;
    }

    public void setEndpointInstance(String endpointInstance) {
        this.endpointInstance = endpointInstance;
    }

    public String getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public boolean isConsumerOnly() {
        return consumerOnly;
    }

    public void setConsumerOnly(boolean consumerOnly) {
        this.consumerOnly = consumerOnly;
    }

    public boolean isProducerOnly() {
        return producerOnly;
    }

    public void setProducerOnly(boolean producerOnly) {
        this.producerOnly = producerOnly;
    }

    @Override
    public String toString() {
        return endpointUri;
    }
}
