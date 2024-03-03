Example for running web application under websphere 8 container

https://hub.docker.com/r/ibmcom/websphere-traditional

When running container image, expose 9043 and 9080 tcp ports. Application
listen on 9080 port. Admin console is on 9043.

To login to ws console: https://localhost:9043/ibm/console/login.do?action=secure

Extract the password of 'wsadmin' from /tmp/PASSWORD

https://publib.boulder.ibm.com/httpserv/cookbook/Containers-WebSphere_Application_Server_traditional_in_Containers.html

https://www.ibm.com/support/pages/lifecycle-policy-websphere-application-server-traditional
