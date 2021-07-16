/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package api.models.requests

import cats.data.EitherT
import play.api.mvc.QueryStringBindable

object Binders {

  type BinderResult[A] = EitherT[Option, String, A]

  def bindable[T](implicit binder: QueryStringBindable[T]) = binder

  def bind[T](key: String, params: Map[String, Seq[String]])(implicit binder: QueryStringBindable[T]): BinderResult[T] =
    EitherT(binder.bind(key, params))

  def bind[T](default: => T)(key: String, params: Map[String, Seq[String]])(implicit binder: QueryStringBindable[T]): BinderResult[T] =
    EitherT(binder.bind(key, params).orElse(Some(Right(default))))

  def unbind[T](key: String, value: T)(implicit binder: QueryStringBindable[T]): String =
    binder.unbind(key, value)

  def unbind[T](default: T)(key: String, value: T)(implicit binder: QueryStringBindable[T]): String =
    if (value == default) "" else binder.unbind(key, value)

}
