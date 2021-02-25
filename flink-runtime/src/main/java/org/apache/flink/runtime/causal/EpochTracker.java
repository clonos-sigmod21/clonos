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

package org.apache.flink.runtime.causal;


import org.apache.flink.runtime.causal.recovery.IRecoveryManager;
import org.apache.flink.runtime.state.CheckpointListener;

public interface EpochTracker {

	long getCurrentEpoch();

	int getRecordCount();

	/**
	 * Called AFTER each input record is processed.
	 * For correctness, it is important that all calls to this method are done under the checkpoint lock
	 */
	void incRecordCount();

	void startNewEpoch(long epochID);

	void setRecordCountTarget(int target);

	void setRecoveryManager(IRecoveryManager recoveryManager);

	void subscribeToEpochStartEvents(EpochStartListener listener);

	void subscribeToCheckpointCompleteEvents(CheckpointListener listener);

    void notifyCheckpointComplete(long checkpointId);
}
