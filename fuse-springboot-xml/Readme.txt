This is a simple fuse application running in spring boot.

Its purposo is to demonstrate how to manage incoming xml input files with Jaxb.

Maven will manage the JAXB generation of java classes from the xsd.

If you need those classes in order to develop, use xjc as:

`xjc.sh -p com.model books.xsd -d .`

this will generate some java classes annotated in the package com.model.

