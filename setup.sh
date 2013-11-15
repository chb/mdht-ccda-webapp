wget  "http://downloads.sourceforge.net/project/oht-modeling/Nightly%20Builds/Runtime/org.openhealthtools.mdht.uml.cda.runtime.N-N20130918-201309181634.zip" -O mdht.zip
unzip mdht.zip -d lib
mv lib/org.openhealthtools.mdht.uml.cda.consol.runtime.201212071459/1.2.0_Runtime/*.jar lib/
python install-to-project-repo/install-to-project-repo.py  
mvn package
