package br.com.leonardo.context.resolver;

import java.util.Optional;

public interface Resolver<T, I> {
    void add(T type);
    Optional<T> get(I id);
}
