# quarkus-order-processor

Example Quarkus application that:

* receives commands via messaging channel to create new orders on a traditional RDBMS
* emit events on a publish-subscribe channel to inform other applications that an order was received

It uses XML to represent commands and google protobuf to represent events

For PostgreSQL set POSTGRESQL_MAX_PREPARED_TRANSACTIONS=63 in order to use XA transaction
