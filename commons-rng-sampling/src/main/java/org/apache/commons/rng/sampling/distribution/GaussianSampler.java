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
package org.apache.commons.rng.sampling.distribution;

import org.apache.commons.rng.UniformRandomProvider;

/**
 * Sampling from a Gaussian distribution with given mean and
 * standard deviation.
 *
 * @since 1.1
 */
public class GaussianSampler implements SharedStateContinuousSampler {
    /** Mean. */
    private final double mean;
    /** standardDeviation. */
    private final double standardDeviation;
    /** Normalized Gaussian sampler. */
    private final NormalizedGaussianSampler normalized;

    /**
     * @param normalized Generator of N(0,1) Gaussian distributed random numbers.
     * @param mean Mean of the Gaussian distribution.
     * @param standardDeviation Standard deviation of the Gaussian distribution.
     * @throws IllegalArgumentException if {@code standardDeviation <= 0}
     */
    public GaussianSampler(NormalizedGaussianSampler normalized,
                           double mean,
                           double standardDeviation) {
        if (standardDeviation <= 0) {
            throw new IllegalArgumentException(
                "standard deviation is not strictly positive: " + standardDeviation);
        }
        this.normalized = normalized;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    /**
     * @param rng Generator of uniformly distributed random numbers.
     * @param source Source to copy.
     */
    private GaussianSampler(UniformRandomProvider rng,
                            GaussianSampler source) {
        this.mean = source.mean;
        this.standardDeviation = source.standardDeviation;
        this.normalized = InternalUtils.newNormalizedGaussianSampler(source.normalized, rng);
    }

    /** {@inheritDoc} */
    @Override
    public double sample() {
        return standardDeviation * normalized.sample() + mean;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Gaussian deviate [" + normalized.toString() + "]";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: This function is available if the underlying {@link NormalizedGaussianSampler}
     * is a {@link SharedStateSampler}. Otherwise a run-time exception is thrown.</p>
     *
     * @throws UnsupportedOperationException if the underlying sampler is not a
     * {@link SharedStateSampler} or does not return a {@link NormalizedGaussianSampler} when
     * sharing state.
     */
    @Override
    public SharedStateContinuousSampler withUniformRandomProvider(UniformRandomProvider rng) {
        return new GaussianSampler(rng, this);
    }

    /**
     * Create a new normalised Gaussian sampler.
     *
     * <p>Note: The shared-state functionality is available if the {@link NormalizedGaussianSampler}
     * is a {@link SharedStateSampler}. Otherwise a run-time exception will be thrown when the
     * sampler is used to share state.</p>
     *
     * @param normalized Generator of N(0,1) Gaussian distributed random numbers.
     * @param mean Mean of the Gaussian distribution.
     * @param standardDeviation Standard deviation of the Gaussian distribution.
     * @return the sampler
     * @throws IllegalArgumentException if {@code standardDeviation <= 0}
     * @see #withUniformRandomProvider(UniformRandomProvider)
     */
    public static SharedStateContinuousSampler of(NormalizedGaussianSampler normalized,
                                                  double mean,
                                                  double standardDeviation) {
        return new GaussianSampler(normalized, mean, standardDeviation);
    }
}
