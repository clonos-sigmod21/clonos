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

package org.apache.flink.runtime.causal.log.job.serde;

import org.apache.flink.runtime.causal.log.job.CausalLogID;
import org.apache.flink.runtime.io.network.partition.consumer.InputChannelID;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBufAllocator;

/**
 * Packages the logic for serializing and deserializing causal log deltas.
 */
public interface DeltaSerializerDeserializer {
	/**
	 * Takes a data buffer and piggybacks the appropriate causal log deltas.
	 * @param serialized the data buffer to piggyback onto
	 * @param outputChannelID the output channel to send deltas to. Used for causal log offsets.
	 * @param epochID the epoch at which the downstream is.
	 * @param alloc
	 * @return the data buffer with piggybacked deltas, ready to be sent.
	 */
    ByteBuf enrichWithCausalLogDelta(ByteBuf serialized, InputChannelID outputChannelID, long epochID,
									 ByteBufAllocator alloc);

	/**
	 * Deserializes the piggybacked thread causal deltas.
	 * @param msg the buffer to be deserialized
	 */
	void processCausalLogDelta(ByteBuf msg);

	/**
	 * Notifies the Serializer of what causalLogIDs the output channel consumes
	 */
    void registerDownstreamConsumer(InputChannelID outputChannelID, CausalLogID consumedCausalLog);
}
