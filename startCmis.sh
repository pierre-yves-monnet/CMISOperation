#!/bin/sh

echo "Start CMIS 1.2.0"
# properties.path=/etc/bonita/CoreModel.properties

CLASSPATH="/tmp/cmis/v2/activation-1.1.jar;/tmp/cmis/v2//tmp/cmis/v2/asm-3.3.1.jar;/tmp/cmis/v2/chemistry-opencmis-client-api-1.0.0.jar;/tmp/cmis/v2/chemistry-opencmis-client-bindings-1.0.0.jar;/tmp/cmis/v2/chemistry-opencmis-client-impl-1.0.0.jar;/tmp/cmis/v2/chemistry-opencmis-commons-api-1.0.0.jar;/tmp/cmis/v2/chemistry-opencmis-commons-impl-1.0.0.jar;/tmp/cmis/v2/commons-api-0.0.1-SNAPSHOT.jar;/tmp/cmis/v2/commons-jar-0.0.1-SNAPSHOT.jar;/tmp/cmis/v2/cxf-core-3.0.10.jar;/tmp/cmis/v2/cxf-rt-bindings-soap-3.0.10.jar;/tmp/cmis/v2/cxf-rt-bindings-xml-3.0.10.jar;/tmp/cmis/v2/cxf-rt-databinding-jaxb-3.0.10.jar;/tmp/cmis/v2/cxf-rt-frontend-jaxws-3.0.10.jar;/tmp/cmis/v2/cxf-rt-frontend-simple-3.0.10.jar;/tmp/cmis/v2/cxf-rt-transports-http-3.0.10.jar;/tmp/cmis/v2/cxf-rt-ws-addr-3.0.10.jar;/tmp/cmis/v2/cxf-rt-ws-policy-3.0.10.jar;/tmp/cmis/v2/cxf-rt-wsdl-3.0.10.jar;/tmp/cmis/v2/FastInfoset-1.2.12.jar;/tmp/cmis/v2/jaxb-api-2.1.jar;/tmp/cmis/v2/jaxb-core-2.1.14.jar;/tmp/cmis/v2/jaxb-impl-2.1.14.jar;/tmp/cmis/v2/json-20140107.jar;/tmp/cmis/v2/neethi-3.0.3.jar;/tmp/cmis/v2/stax2-api-3.1.4.jar;/tmp/cmis/v2/woodstox-core-asl-4.4.1.jar;/tmp/cmis/v2/wsdl4j-1.6.3.jar;/tmp/cmis/v2/xml-resolver-1.2.jar;/tmp/cmis/v2/xmlschema-core-2.2.1.jar"

echo $CLASSPATH

java  -classpath ${CLASSPATH}  -jar CMISCall-1.2.0-jar-with-dependencies.jar http://ledcb970.frmon.danet:8080 meirami bpm '' 6779299849492924173 working /etc/bonita/CoreModel.properties 0
