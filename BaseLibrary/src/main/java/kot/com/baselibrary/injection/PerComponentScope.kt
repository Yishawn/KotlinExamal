package kot.com.baselibrary.injection

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope

/**
 *这是一个组件级别使用的作用域标示
 *
 * @auth dyx on 2018/12/4.
 */
/*
    组件级别 作用域
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class PerComponentScope