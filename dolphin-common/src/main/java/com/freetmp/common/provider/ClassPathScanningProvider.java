/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freetmp.common.provider;

import com.freetmp.common.resource.*;
import com.freetmp.common.type.AnnotationMetadata;
import com.freetmp.common.type.classreading.CachingMetadataReaderFactory;
import com.freetmp.common.type.classreading.MetadataReader;
import com.freetmp.common.type.classreading.MetadataReaderFactory;
import com.freetmp.common.type.filter.TypeFilter;
import com.freetmp.common.util.Assert;
import com.freetmp.common.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/*
 * A component provider that scans the classpath from a base package. It then
 * applies exclude and include filters to the resulting classes to find candidates.
 * <p/>
 * <p>This implementation is based on Spring's
 * {@link com.freetmp.common.type.classreading.MetadataReader MetadataReader}
 * facility, backed by an ASM {@link com.freetmp.common.asm.ClassReader ClassReader}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Chris Beams
 * @see com.freetmp.common.type.classreading.MetadataReaderFactory
 * @see com.freetmp.common.type.AnnotationMetadata
 * @since 2.5
 */
public class ClassPathScanningProvider {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory =
            new CachingMetadataReaderFactory(this.resourcePatternResolver);

    private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

    public ClassPathScanningProvider() {

    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    /*
     * Return the ResourceLoader that this component provider uses.
     */
    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    /*
     * Set the {@link MetadataReaderFactory} to use.
     * <p>Default is a {@link CachingMetadataReaderFactory} for the specified
     * {@linkplain #setResourceLoader resource loader}.
     * <p>Call this setter method <i>after</i> {@link #setResourceLoader} in order
     * for the given MetadataReaderFactory to override the default factory.
     */
    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }

    /*
     * Return the MetadataReaderFactory used by this component provider.
     */
    public final MetadataReaderFactory getMetadataReaderFactory() {
        return this.metadataReaderFactory;
    }

    /*
     * Set the resource pattern to use when scanning the classpath.
     * This value will be appended to each base package name.
     *
     * @see #findCandidateComponents(String)
     * @see #DEFAULT_RESOURCE_PATTERN
     */
    public void setResourcePattern(String resourcePattern) {
        Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
        this.resourcePattern = resourcePattern;
    }

    /*
     * Add an include type filter to the <i>end</i> of the inclusion list.
     */
    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    /*
     * Add an exclude type filter to the <i>front</i> of the exclusion list.
     */
    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters() {
        this.includeFilters.clear();
        this.excludeFilters.clear();
    }

    /*
     * Scan the class path for candidate components.
     *
     * @param basePackage the package to check for annotated classes
     * @return a corresponding Set of autodetected bean definitions
     */
    public Set<MetadataReader> findCandidateComponents(String basePackage) throws IOException {
        Set<MetadataReader> candidates = new LinkedHashSet<>();

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + this.resourcePattern;
        Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

        for (Resource resource : resources) {
            logger.trace("Scanning " + resource);
            if (resource.isReadable()) {
                try {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        candidates.add(metadataReader);
                    }
                } catch (IOException e) {
                    logger.warn("Error reading " + resource, e);
                }
            } else {
                logger.trace("Ignored because not readable: " + resource);
            }
        }
        return candidates;
    }


    /*
     * Resolve the specified base package into a pattern specification for
     * the package search path.
     * <p>The default implementation resolves placeholders against system properties,
     * and converts a "."-based package path to a "/"-based resource path.
     *
     * @param basePackage the base package as specified by the user
     * @return the pattern specification to be used for package searching
     */
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }

    /*
     * Determine whether the given class does not match any exclude filter
     * and does match at least one include filter.
     *
     * @param metadataReader the ASM ClassReader for the class
     * @return whether the class qualifies as a candidate component
     */
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Determine whether the given bean definition qualifies as candidate.
     * <p>The default implementation checks whether the class is concrete
     * (i.e. not abstract and not an interface). Can be overridden in subclasses.
     *
     * @param metadata the bean metadata to check
     * @return whether the bean definition qualifies as a candidate component
     */
    protected boolean isCandidateComponent(AnnotationMetadata metadata) {
        return (metadata.isConcrete() && metadata.isIndependent());
    }


    /*
     * Clear the underlying metadata cache, removing all cached class metadata.
     */
    public void clearCache() {
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }

}
