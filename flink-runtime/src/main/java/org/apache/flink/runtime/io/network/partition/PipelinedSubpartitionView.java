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

package org.apache.flink.runtime.io.network.partition;

import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.causal.VertexID;
import org.apache.flink.runtime.io.network.partition.ResultSubpartition.BufferAndBacklog;

import javax.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * View over a pipelined in-memory only subpartition.
 */
class PipelinedSubpartitionView implements ResultSubpartitionView {

	private static final Logger LOG = LoggerFactory.getLogger(PipelinedSubpartitionView.class);

	/** The subpartition this view belongs to. */
	private final PipelinedSubpartition parent;

	private BufferAvailabilityListener availabilityListener;

	/** Flag indicating whether this view has been released. */
	private final AtomicBoolean isReleased;

	PipelinedSubpartitionView(PipelinedSubpartition parent, BufferAvailabilityListener listener) {
		this.parent = checkNotNull(parent);
		this.availabilityListener = checkNotNull(listener);
		this.isReleased = new AtomicBoolean();
	}

	public void setAvailabilityListener(BufferAvailabilityListener availabilityListener) {
		this.availabilityListener = availabilityListener;
	}

	@Nullable
	@Override
	public BufferAndBacklog getNextBuffer() {
		return parent.pollBuffer();
	}

	@Override
	public void notifyDataAvailable() {
		availabilityListener.notifyDataAvailable();
	}

	@Override
	public void notifySubpartitionConsumed() {
		LOG.debug("Notify that {} is consumed.", this);
		releaseAllResources();
	}

	@Override
	public void releaseAllResources() {
		LOG.debug("Release all resources of {}.", this);
		if (isReleased.compareAndSet(false, true)) {
			// The view doesn't hold any resources and the parent cannot be restarted. Therefore,
			// it's OK to notify about consumption as well.
			parent.onConsumedSubpartition();
		}
	}

	@Override
	public void sendFailConsumerTrigger(Throwable cause) {
		parent.sendFailConsumerTrigger(cause);
	}

	@Override
	public boolean isReleased() {
		return isReleased.get() || parent.isReleased();
	}

	@Override
	public boolean nextBufferIsEvent() {
		return parent.nextBufferIsEvent();
	}

	@Override
	public boolean isAvailable() {
		return parent.isAvailable();
	}

	@Override
	public JobID getJobID() {
		return this.parent.getJobID();
	}

	@Override
	public short getVertexID() {
		return this.parent.getVertexID();
	}

	@Override
	public Throwable getFailureCause() {
		return parent.getFailureCause();
	}

	@Override
	public String toString() {
		return String.format("PipelinedSubpartitionView(index: %d) of ResultPartition %s",
				parent.index,
				parent.parent.getPartitionId());
	}
}
