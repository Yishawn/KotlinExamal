package kot.com.baselibrary.injection

/**
 * 生命周期与Activity一致的实例的scope,这个组件是用来给MainActivity注入依赖的，这会提醒我们将MainComponent的创建销毁与MainActivity关联起来。
 *
 * @auth dyx on 2018/12/4.
 */
import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME
import javax.inject.Scope

/**
 * Identifies a type that the injector only instantiates once. Not inherited.
 *
 * @see javax.inject.Scope @Scope
 */
@Scope
@Documented
@Retention(RUNTIME)
annotation class ActivityScope
