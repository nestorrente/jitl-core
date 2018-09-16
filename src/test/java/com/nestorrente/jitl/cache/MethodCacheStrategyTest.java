package com.nestorrente.jitl.cache;

import com.nestorrente.jitl.Jitl;
import com.nestorrente.jitl.template.NoOpTemplateEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore("Not defined yet")
public class MethodCacheStrategyTest {

	/*
	 * TODO idea para tests:
	 *
	 * Lo que deberíamos hacer es, en primer lugar, una clase que testee que el caché y sus
	 * diferentes estrategias funcionan correctamente. Para ello se utilizará una instancia
	 * de Jitl que no utilice caché y una interfaz que no tenga @Cache. En dicha interfaz
	 * habrá un método por cada tipo de caché, y se creará un test por cada uno de ellos
	 * que compruebe que se realizan el número correcto de llamadas a get/cache.
	 *
	 * Hecho esto, podemos crear tests (con diferentes interfaces) que comprueben los siguientes casos:
	 * - En una interfaz sin anotar:
	 *   - Un método sin anotar utiliza la estrategia de caché de Jitl.
	 *   - Un método anotado utiliza su propia estrategia de caché.
	 * - En una interfaz anotada:
	 *   - Un método sin anotar utiliza la estrategia de caché de la interfaz.
	 *   - Un método anotado utiliza su propia estrategia de caché.
	 *
	 * Al final los test se nos quedan en una interfaz con 4 métodos para probar que funciona el caché
	 * y sus estrategias, y otras 2 interfaces, cada una con 2 métodos, para comprobar que Jitl utiliza
	 * la estrategia de caché correcta en cada caso.
	 */

	/*
	 * TODO replantear sistema de caché:
	 *
	 * Tal vez _Deprecated_CacheManager debería ser específico; es decir,
	 * tener métodos getUri/cacheUri y getTemplate/cacheTemplate.
	 * No obstante, esto tiene que delegar en dos cachés (ya sean de Guava, Mapas, o lo que sea).
	 * Otra opción es no poder personalizar la caché. Usar Mapas o Guava, la que queramos,
	 * y no dar opción de configurarlo.
	 */

	/*
	 * TODO tal vez habría que utilizar mockito para testear que las cachés funcionan correctamente.
	 * Es posible que Jitl peque de no permitir meterse dentro de sus entresijos. Habrá que verlo con calma.
	 */

	private Jitl jitl;

	@Before
	public void setup() {

		this.jitl = Jitl.builder()
				.setCacheTemplates(false)
				.setTemplateEngine(NoOpTemplateEngine.getInstance())
				.build();

	}

	@After
	public void cleanup() {
		this.jitl = null;
	}

	@Test
	public void welcomeReceivingGunsAndRosesAndTheJungleReturnsWelcomeToTheJunglePage() {

		DefaultCacheInterface instance = this.jitl.getInstance(DefaultCacheInterface.class);

		instance.noCache();

	}

	public static class Pepito {
		public int pepe() {
			return 3;
		}

		public int otro() {
			return 5;
		}

		public void someMethod(String someArg) {

		}

		public void anotherMethod(Object anotherArg) {

		}
	}

	public static void main(String[] args) {

		Pepito p = mock(Pepito.class);

		when(p.pepe()).thenCallRealMethod();
		when(p.otro()).thenCallRealMethod();

		p.pepe();
		p.pepe();

		p.otro();
		p.otro();
		p.otro();

		p.someMethod("value");

		p.anotherMethod(1);

		verify(p, times(2)).pepe();
		verify(p, times(3)).otro();
		verify(p, atLeastOnce()).someMethod("value");
		verify(p, atLeastOnce()).anotherMethod(anyInt());

	}

}
