/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmagnotta.app;

import javax.enterprise.inject.Produces;

import org.apache.camel.quarkus.main.CamelMainApplication;
import org.gmagnotta.protobuf.OrderInitializerImpl;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.transaction.lookup.JBossStandaloneJTAManagerLookup;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main extends CamelMainApplication {
	
	@Produces
	RemoteCacheManager remoteCacheManager;

	@Override
	public int run(String... args) throws Exception {

		ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
		
		clientBuilder.transaction().transactionManagerLookup(new JBossStandaloneJTAManagerLookup());
		clientBuilder.addServer().host("127.0.0.1").port(36371).security().authentication().username("admin")
				.password("admin").addContextInitializer(new OrderInitializerImpl());

		remoteCacheManager = new RemoteCacheManager(clientBuilder.build());

		return super.run(args);
	}

	public static void main(String... args) throws Exception {

		Quarkus.run(Main.class, args);

	}

}
