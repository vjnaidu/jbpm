/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.wih;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.test.TestStatefulKnowledgeSession;
import org.kie.test.util.db.DataSourceFactory;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.junit.After;
import org.junit.Before;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.task.api.InternalTaskService;

public class HTWorkItemHandlerTest extends HTWorkItemHandlerBaseTest {

    private EntityManagerFactory emf;
    private WorkItemHandler htWorkItemHandler;
    private PoolingDataSourceWrapper pds;
    
    @Before
    public void setUp() throws Exception {
    	pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        ksession = new TestStatefulKnowledgeSession();
        env = EnvironmentFactory.newEnvironment();
        ksession.setEnvironment(env);
        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
				.entityManagerFactory(emf)
				.getTaskService();
        htWorkItemHandler = new NonManagedLocalHTWorkItemHandler(ksession, taskService);
 
        setHandler(htWorkItemHandler);
    }

    @After
    public void tearDown() throws Exception {
        int removeAllTasks = ((InternalTaskService)taskService).removeAllTasks();
		if (emf != null) {
			emf.close();
		}
		if (pds != null) {
			pds.close();
		}

    }

    protected PoolingDataSourceWrapper setupPoolingDataSource() {
        Properties driverProperties = new Properties();
        driverProperties.put("user", "sa");
        driverProperties.put("password", "");
        driverProperties.put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
        driverProperties.put("driverClassName", "org.h2.Driver");
        driverProperties.put("className", "org.h2.jdbcx.JdbcDataSource");

        PoolingDataSourceWrapper pds = DataSourceFactory.setupPoolingDataSource("jdbc/jbpm-ds", driverProperties);
        return pds;
    }
}
