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

package org.apache.commons.rng.examples.jmh.distribution;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.examples.jmh.RandomSources;
import org.apache.commons.rng.sampling.distribution.AhrensDieterExponentialSampler;
import org.apache.commons.rng.sampling.distribution.AhrensDieterMarsagliaTsangGammaSampler;
import org.apache.commons.rng.sampling.distribution.BoxMullerNormalizedGaussianSampler;
import org.apache.commons.rng.sampling.distribution.ChengBetaSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import org.apache.commons.rng.sampling.distribution.InverseTransformParetoSampler;
import org.apache.commons.rng.sampling.distribution.LogNormalSampler;
import org.apache.commons.rng.sampling.distribution.MarsagliaNormalizedGaussianSampler;
import org.apache.commons.rng.sampling.distribution.ZigguratNormalizedGaussianSampler;
import org.apache.commons.rng.simple.RandomSource;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * Executes benchmark to compare the speed of generation of random numbers
 * from the various source providers for different types of {@link ContinuousSampler}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms128M", "-Xmx128M"})
public class ContinuousSamplersPerformance {
    /**
     * The {@link ContinuousSampler} samplers to use for testing. Creates the sampler for each
     * {@link RandomSource} in the default {@link RandomSources}.
     */
    @State(Scope.Benchmark)
    public static class Sources extends RandomSources {
        /**
         * The sampler type.
         */
        @Param({"BoxMullerNormalizedGaussianSampler",
                "MarsagliaNormalizedGaussianSampler",
                "ZigguratNormalizedGaussianSampler",
                "AhrensDieterExponentialSampler",
                "AhrensDieterMarsagliaTsangGammaSampler",
                "LogNormalBoxMullerNormalizedGaussianSampler",
                "LogNormalMarsagliaNormalizedGaussianSampler",
                "LogNormalZigguratNormalizedGaussianSampler",
                "ChengBetaSampler",
                "ContinuousUniformSampler",
                "InverseTransformParetoSampler",
                })
        private String samplerType;

        /** The sampler. */
        private ContinuousSampler sampler;

        /**
         * @return the sampler.
         */
        public ContinuousSampler getSampler() {
            return sampler;
        }

        /** Instantiates sampler. */
        @Override
        @Setup
        public void setup() {
            super.setup();
            final UniformRandomProvider rng = getGenerator();
            if ("BoxMullerNormalizedGaussianSampler".equals(samplerType)) {
                sampler = new BoxMullerNormalizedGaussianSampler(rng);
            } else if ("MarsagliaNormalizedGaussianSampler".equals(samplerType)) {
                sampler = new MarsagliaNormalizedGaussianSampler(rng);
            } else if ("ZigguratNormalizedGaussianSampler".equals(samplerType)) {
                sampler = new ZigguratNormalizedGaussianSampler(rng);
            } else if ("AhrensDieterExponentialSampler".equals(samplerType)) {
                sampler = new AhrensDieterExponentialSampler(rng, 4.56);
            } else if ("AhrensDieterMarsagliaTsangGammaSampler".equals(samplerType)) {
                sampler = new AhrensDieterMarsagliaTsangGammaSampler(rng, 9.8, 0.76);
            } else if ("LogNormalBoxMullerNormalizedGaussianSampler".equals(samplerType)) {
                sampler = new LogNormalSampler(new BoxMullerNormalizedGaussianSampler(rng), 12.3, 4.6);
            } else if ("LogNormalMarsagliaNormalizedGaussianSampler".equals(samplerType)) {
                sampler = new LogNormalSampler(new MarsagliaNormalizedGaussianSampler(rng), 12.3, 4.6);
            } else if ("LogNormalZigguratNormalizedGaussianSampler".equals(samplerType)) {
                sampler = new LogNormalSampler(new ZigguratNormalizedGaussianSampler(rng), 12.3, 4.6);
            } else if ("ChengBetaSampler".equals(samplerType)) {
                sampler = new ChengBetaSampler(rng, 0.45, 6.7);
            } else if ("ContinuousUniformSampler".equals(samplerType)) {
                sampler = new ContinuousUniformSampler(rng, 123.4, 5678.9);
            } else if ("InverseTransformParetoSampler".equals(samplerType)) {
                sampler = new InverseTransformParetoSampler(rng, 23.45, 0.1234);
            }
        }
    }

    // Benchmarks methods below.

    /**
     * The value.
     *
     * <p>This must NOT be final!</p>
     */
    private double value;

    /**
     * Baseline for the JMH timing overhead for production of an {@code double} value.
     *
     * @return the {@code double} value
     */
    @Benchmark
    public double baseline() {
        return value;
    }

    /**
     * Run the sampler.
     *
     * @param sources Source of randomness.
     * @return the sample value
     */
    @Benchmark
    public double sample(Sources sources) {
        return sources.getSampler().sample();
    }
}
