package com.nestorrente.jitl.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class OptionalUtils {

	/**
	 * Method taken from JDK 9 source code.
	 * <p>
	 * If a value is present in parameter {@code optional}, returns an {@code Optional}
	 * describing the value, otherwise returns an {@code Optional} produced by the
	 * supplying function.
	 *
	 * @param optional Initial optional
	 * @param supplier the supplying function that produces an {@code Optional}
	 *                 to be returned
	 * @return returns an {@code Optional} describing the value of parameter
	 * {@code optional}, if a value is present, otherwise an
	 * {@code Optional} produced by the supplying function.
	 * @throws NullPointerException if {@code optional} is null or the supplying
	 *                              function is {@code null} or produces a {@code null} result
	 */
	public static <T> Optional<T> or(Optional<T> optional, Supplier<? extends Optional<? extends T>> supplier) {
		Objects.requireNonNull(supplier);
		if(optional.isPresent()) {
			return optional;
		} else {
			@SuppressWarnings("unchecked")
			Optional<T> r = (Optional<T>) supplier.get();
			return Objects.requireNonNull(r);
		}
	}

	public static <T> Optional<T> or(Optional<T> optional, Supplier<? extends Optional<? extends T>>... suppliers) {

		Optional<T> current = optional;

		for(Supplier<? extends Optional<? extends T>> supplier : suppliers) {
			current = or(current, supplier);
		}

		return current;

	}

	public static <T> Stream<T> stream(Optional<T[]> optional) {
		return optional.map(Arrays::stream).orElseGet(Stream::empty);
	}

	public static <T> Stream<T> streamSingle(Optional<T> optional) {
		return optional.map(Stream::of).orElseGet(Stream::empty);
	}

}
