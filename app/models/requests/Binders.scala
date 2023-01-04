/*
 * Copyright 2023 HM Revenue & Customs
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

package models.requests

import cats.data.EitherT
import play.api.mvc.QueryStringBindable

object Binders {

  type BinderResult[A] = EitherT[Option, String, A]

  /** A binder that will always pass with the value provided.
    *
    * {{
    *
    * val result = successful[Int](42)
    * val value = result.value // Some(Right(42))
    *
    * }}
    *
    * This is the same as `pure` in [[cats.Applicative]].
    *
    * @param a the value to be returned by the BinderResult
    * @return An EitherT[Option, String, A] that is always a EitherT(Some(Right(a)))
    */
  def successful[A](a: => A): BinderResult[A] = EitherT[Option, String, A](Some(Right(a)))

  /** A binder that will always fail with the message provided.
    *
    * {{
    *
    * val result = failed[Int]("Did not pass")
    * val value = result.value // Some(Left("Did not pass"))
    *
    * }}
    *
    * @param message the message that will be returned by the BinderResult
    * @return An EitherT[Option, String, A] that is always a EitherT(Some(Left(message)))
    */
  def failed[A](message: String): BinderResult[A] = EitherT[Option, String, A](Some(Left(message)))

  /** A binder that will return a None.
    *
    *  This is useful in a flatMap.
    *
    * {{
    *
    * val result = ignore[Int]
    * val value = result.value // None
    *
    * }}
    *
    * @param message the message that will be returned by the BinderResult
    * @return An EitherT[Option, String, A] that is always a EitherT(Some(Left(message)))
    */
  def ignore[A]: BinderResult[A] = EitherT[Option, String, A](None)

  /** Implicitly resolve for a QueryStringBindable[T] at the call site.
    */
  def bindable[T](implicit binder: QueryStringBindable[T]): QueryStringBindable[T] = binder

  def bind[T](key: String, params: Map[String, Seq[String]])(implicit binder: QueryStringBindable[T]): BinderResult[T] =
    EitherT(binder.bind(key, params))

  def bind[T](default: => T)(key: String, params: Map[String, Seq[String]])(implicit binder: QueryStringBindable[T]): BinderResult[T] =
    EitherT(binder.bind(key, params).orElse(Some(Right(default))))

  def unbind[T](key: String, value: T)(implicit binder: QueryStringBindable[T]): String =
    binder.unbind(key, value)

  def unbind[T](default: T)(key: String, value: T)(implicit binder: QueryStringBindable[T]): String =
    if (value == default) "" else binder.unbind(key, value)

}
