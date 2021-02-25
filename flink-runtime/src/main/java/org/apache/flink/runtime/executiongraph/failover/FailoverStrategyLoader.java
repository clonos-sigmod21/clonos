/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.executiongraph.failover;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.IllegalConfigurationException;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.util.StringUtils;

import org.slf4j.Logger;

import javax.annotation.Nullable;

/**
 * A utility class to load failover strategies from the configuration. 
 */
public class FailoverStrategyLoader {

	/** Config name for the {@link RestartAllStrategy} */
	public static final String FULL_RESTART_STRATEGY_NAME = "full";

	/** Config name for the {@link RestartIndividualStrategy} */
	public static final String INDIVIDUAL_RESTART_STRATEGY_NAME = "individual";

	/** Config name for the {@link RestartPipelinedRegionStrategy} */
	public static final String PIPELINED_REGION_RESTART_STRATEGY_NAME = "region";

	/** Config name for the {@link RunStandbyTaskStrategy} */
	public static final String STANDBY_TASK_RUN_STRATEGY_NAME = "standbytask";

	// ------------------------------------------------------------------------

	/**
	 * Loads a FailoverStrategy Factory from the given configuration.
	 */
	public static FailoverStrategy.Factory loadFailoverStrategy(Configuration config, @Nullable Logger logger) {
		final String strategyParam = config.getString(JobManagerOptions.EXECUTION_FAILOVER_STRATEGY);

		if (StringUtils.isNullOrWhitespaceOnly(strategyParam)) {
			if (logger != null) {
				logger.warn("Null config value for {} ; using default failover strategy (full restarts).",
						JobManagerOptions.EXECUTION_FAILOVER_STRATEGY.key());
			}

			return new RestartAllStrategy.Factory();
		}
		else {
			switch (strategyParam.toLowerCase()) {
				case FULL_RESTART_STRATEGY_NAME:
					return new RestartAllStrategy.Factory();

				case PIPELINED_REGION_RESTART_STRATEGY_NAME:
					return new RestartPipelinedRegionStrategy.Factory();

				case INDIVIDUAL_RESTART_STRATEGY_NAME:
					return new RestartIndividualStrategy.Factory();

				case STANDBY_TASK_RUN_STRATEGY_NAME:
					final int numStandbyTasksToMaintain = config.getInteger(JobManagerOptions.NUMBER_OF_STANDBY_TASKS_TO_MAINTAIN);
					final int checkpointCoordinatorBackoffMultiplier = config.getInteger(JobManagerOptions.CC_BACKOFF_MULT);
					final long checkpointCoordinatorBackoffBase = config.getLong(JobManagerOptions.CC_BACKOFF_BASE);
					return new RunStandbyTaskStrategy.Factory(numStandbyTasksToMaintain, checkpointCoordinatorBackoffMultiplier, checkpointCoordinatorBackoffBase);

				default:
					// we could interpret the parameter as a factory class name and instantiate that
					// for now we simply do not support this
					throw new IllegalConfigurationException("Unknown failover strategy: " + strategyParam);
			}
		}
	}
}
