/*
 * Copyright 2007-2011 WorldWide Conferencing, LLC
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

package net.liftweb
package sitemap

import common._
import org.specs2.mutable.Specification


/**
 * Systems under specification for Loc.
 */
object LocSpec extends Specification  {
  "Loc Specification".title

  case class Param(s: String)

  "A Loc" should {

    "calculate default href for basic menu definition" in {
      val loc = (Menu("Test") / "foo" / "bar").toMenu.loc
      loc.calcDefaultHref mustEqual "/foo/bar"
    }

    "calculate href for menu with parameters" in {
      val loc = (Menu.param[Param]("Test", "Test", s => Full(Param(s)), p => p.s) / "foo" / "bar" / *).toLoc
      loc.calcHref(Param("myparam")) mustEqual "/foo/bar/myparam"
    }
  }
}

