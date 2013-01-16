wget  "http://downloads.sourceforge.net/project/oht-modeling/Releases/Runtime/org.openhealthtools.mdht.uml.cda.consol.runtime.R20121220-1425.zip" -O mdht.zip
unzip mdht.zip -d lib
mv lib/org.openhealthtools.mdht.uml.cda.consol.runtime.201212071459/1.2.0_Runtime/*.jar lib/
python install-to-project-repo/install-to-project-repo.py  
mvn package
