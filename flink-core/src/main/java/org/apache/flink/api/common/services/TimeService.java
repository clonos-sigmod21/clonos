/*
 *
 *
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 *
 *
 */

package org.apache.flink.api.common.services;

import org.apache.flink.annotation.Public;
import org.apache.flink.api.common.functions.RuntimeContext;

/**
 * A service that is exposed in the {@link RuntimeContext}, so that UDFs may safely utilize the current time in their
 * functions, in such a way that after local recovery, the state of the task is consistent with the state before failure
 */
@Public
public interface TimeService {
	/**
	 * During normal operation, returns the current system time.
	 * During causal recovery, if the implementation is causal, returns the same timestamp as before.
	 */
    long currentTimeMillis();
}
