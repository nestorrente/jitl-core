package com.nestorrente.jitl.cache;

public enum CacheStrategy {

	/**
	 * Don't use cache.
	 * <p>
	 * Recommended for the following scenarios:
	 * <ul>
	 * <li>low-memory environments.</li>
	 * <li>Jitl is going to be invoked occasionally.</li>
	 * </ul>
	 */
	NONE,

	/**
	 * Cache template resource URI.
	 * <p>
	 * Good balance between execution speed and memory use.
	 */
	URI,

	/**
	 * Cache template contents.
	 * <p>
	 * Recommended for high-memory environments that require fast execution speed and Jitl is going to be invoked frequently.
	 */
	CONTENTS

}
