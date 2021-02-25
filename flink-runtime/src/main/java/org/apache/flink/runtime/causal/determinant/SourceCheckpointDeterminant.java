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

package org.apache.flink.runtime.causal.determinant;

import org.apache.flink.runtime.causal.recovery.RecoveryManager;
import org.apache.flink.runtime.causal.recovery.RecoveryManagerContext;
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.checkpoint.CheckpointType;
import org.apache.flink.runtime.state.CheckpointStorageLocationReference;

import java.util.Arrays;

public class SourceCheckpointDeterminant extends AsyncDeterminant {

	private byte[] storageReference;
	private long checkpointID;
	private long checkpointTimestamp;
	private CheckpointType type;

	public SourceCheckpointDeterminant() {
		super();
	}

	public SourceCheckpointDeterminant(int recordCount, long checkpointID, long checkpointTimestamp,
									   CheckpointType type, byte[] storageReference) {
		super(recordCount);
		this.checkpointID = checkpointID;
		this.checkpointTimestamp = checkpointTimestamp;
		this.type = type;
		this.storageReference = storageReference;
	}


	public byte[] getStorageReference() {
		return storageReference;
	}

	public long getCheckpointID() {
		return checkpointID;
	}

	public long getCheckpointTimestamp() {
		return checkpointTimestamp;
	}

	public CheckpointType getType() {
		return type;
	}

	public SourceCheckpointDeterminant replace(int recordCount, long checkpointID, long checkpointTimestamp,
											   CheckpointType type, byte[] storageReference) {
		super.replace(recordCount);
		this.checkpointID = checkpointID;
		this.checkpointTimestamp = checkpointTimestamp;
		this.type = type;
		this.storageReference = storageReference;
		return this;
	}


	@Override
	public void process(RecoveryManagerContext recoveryManagerContext) {
		CheckpointMetrics metrics = new CheckpointMetrics()
			.setBytesBufferedInAlignment(0L)
			.setAlignmentDurationNanos(0L);

		try {
			recoveryManagerContext.getCheckpointForceable().performCheckpoint(
				new CheckpointMetaData(checkpointID, checkpointTimestamp),
				new CheckpointOptions(type, (storageReference.length > 0 ?
					new CheckpointStorageLocationReference(storageReference) :
					CheckpointStorageLocationReference.getDefault())),
				metrics);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "SourceCheckpointDeterminant{" +
			"storageReference=" + Arrays.toString(storageReference) +
			", checkpointID=" + checkpointID +
			", checkpointTimestamp=" + checkpointTimestamp +
			", type=" + type +
			", recordCount=" + recordCount +
			'}';
	}

	@Override
	public int getEncodedSizeInBytes() {
		//An extra byte is used to encode if storage reference exists
		return super.getEncodedSizeInBytes() + Long.BYTES + Long.BYTES + Byte.BYTES + Byte.BYTES +
			(storageReference != null ? Integer.BYTES + storageReference.length : 0);
	}

	public static byte getTypeTag() {
		return SOURCE_CHECKPOINT_DETERMINANT;
	}
}
