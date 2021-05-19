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

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.camel.quarkus.main.CamelMainApplication;
import org.jboss.logging.Logger;

@QuarkusMain
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String... args) throws Exception {

        if (args.length == 3 && args[0].equals("generate")) {

            int iterations = Integer.valueOf(args[1]);

            XmlGenerator xmlGenerator = new XmlGenerator();

            xmlGenerator.generateFile(iterations, args[2]);

        } else {

            Quarkus.run(CamelMainApplication.class, args);

        }
    }
}
