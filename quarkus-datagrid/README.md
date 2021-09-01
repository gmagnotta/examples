# quarkus-datagrid

Example Quarkus application that:

* receive events on a publish-subscribe channel representing orders and persist them on Red Hat Datagrid
* receives commands via messaging channel to interact with Red Hat Datagrid

It uses XML to represent commands and google protobuf to represent events
