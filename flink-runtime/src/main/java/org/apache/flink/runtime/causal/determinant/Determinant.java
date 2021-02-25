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
package org.apache.flink.runtime.causal.determinant;

public abstract class Determinant {

	//Synchronous
	public static final byte ORDER_DETERMINANT_TAG = 0;
	public static final byte TIMESTAMP_DETERMINANT_TAG = 1;
	public static final byte RNG_DETERMINANT_TAG = 2;
	public static final byte SERIALIZABLE_DETERMINANT_TAG = 3;

	//Asynchronous
	public static final byte TIMER_TRIGGER_DETERMINANT = 4;
	public static final byte SOURCE_CHECKPOINT_DETERMINANT = 5;
	public static final byte IGNORE_CHECKPOINT_DETERMINANT = 6;

	//Output
	public static final byte BUFFER_BUILT_TAG = 7;


	public boolean isOrderDeterminant() {
		return getClass() == OrderDeterminant.class;
	}

	public OrderDeterminant asOrderDeterminant() {
		return (OrderDeterminant) this;
	}

	public boolean isTimestampDeterminant() {
		return getClass() == TimestampDeterminant.class;
	}

	public TimestampDeterminant asTimestampDeterminant() {
		return (TimestampDeterminant) this;
	}

	public boolean isRNGDeterminant() {
		return getClass() == RNGDeterminant.class;
	}

	public RNGDeterminant asRNGDeterminant() {
		return (RNGDeterminant) this;
	}


	public boolean isSerializableDeterminant() {
		return this instanceof SerializableDeterminant;
	}

	public SerializableDeterminant asSerializableDeterminant() {
		return (SerializableDeterminant) this;
	}

	public boolean isBufferBuiltDeterminant() {
		return getClass() == BufferBuiltDeterminant.class;
	}

	public BufferBuiltDeterminant asBufferBuiltDeterminant() {
		return (BufferBuiltDeterminant) this;
	}


	public boolean isTimerTriggerDeterminant() {
		return getClass() == TimerTriggerDeterminant.class;
	}

	public TimerTriggerDeterminant asTimerTriggerDeterminant() {
		return (TimerTriggerDeterminant) this;
	}

	public boolean isSourceCheckpointDeterminant() {
		return getClass() == SourceCheckpointDeterminant.class;
	}

	public SourceCheckpointDeterminant asSourceCheckpointDeterminant() {
		return (SourceCheckpointDeterminant) this;
	}


	public boolean isIgnoreCheckpointDeterminant() {
		return getClass() == IgnoreCheckpointDeterminant.class;
	}

	public IgnoreCheckpointDeterminant asIgnoreCheckpointDeterminant() {
		return (IgnoreCheckpointDeterminant) this;
	}

	public int getEncodedSizeInBytes() {
		return Byte.BYTES;
	}

	public byte getTag() {
		if (this instanceof OrderDeterminant)
			return ORDER_DETERMINANT_TAG;
		if (this instanceof TimestampDeterminant)
			return TIMESTAMP_DETERMINANT_TAG;
		if (this instanceof RNGDeterminant)
			return RNG_DETERMINANT_TAG;
		if (this instanceof SerializableDeterminant)
			return SERIALIZABLE_DETERMINANT_TAG;
		if (this instanceof TimerTriggerDeterminant)
			return TIMER_TRIGGER_DETERMINANT;
		if (this instanceof SourceCheckpointDeterminant)
			return SOURCE_CHECKPOINT_DETERMINANT;
		if (this instanceof IgnoreCheckpointDeterminant)
			return IGNORE_CHECKPOINT_DETERMINANT;
		return BUFFER_BUILT_TAG;
	}

}
