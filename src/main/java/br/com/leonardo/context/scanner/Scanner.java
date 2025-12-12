package br.com.leonardo.context.scanner;

import br.com.leonardo.context.resolver.Resolver;
import org.reflections.Reflections;

public interface Scanner<T extends Resolver> {

    T scan(Reflections reflections);

}
