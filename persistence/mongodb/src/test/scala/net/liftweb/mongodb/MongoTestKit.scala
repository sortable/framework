/*
 * Copyright 2010-2011 WorldWide Conferencing, LLC
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
package mongodb

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterExample


trait MongoTestKit extends Specification with BeforeAfterExample {
  sequential

  def dbName = "lift_"+this.getClass.getName
    .replace("$", "")
    .replace("net.liftweb.mongodb.", "")
    .replace(".", "_")
    .toLowerCase

  def defaultHost = MongoHost("127.0.0.1", 27017)

  // If you need more than one db, override this
  def dbs: List[(MongoIdentifier, MongoHost, String)] = List((DefaultMongoIdentifier, defaultHost, dbName))

  def debug = false

  def before = {
    // define the dbs
    dbs foreach { dbtuple =>
      MongoDB.defineDb(dbtuple._1, MongoAddress(dbtuple._2, dbtuple._3))
    }
  }

  def isMongoRunning: Boolean =
    try {
      if (dbs.length < 1)
        false
      else {
        dbs foreach { dbtuple =>
          MongoDB.use(dbtuple._1) ( db => { db.getLastError } )
        }
        true
      }
    } catch {
      case e: Exception => false
    }

  def checkMongoIsRunning = isMongoRunning must beEqualTo(true).orSkip

  def after = {
    if (!debug && isMongoRunning) {
      // drop the databases
      dbs foreach { dbtuple =>
        MongoDB.use(dbtuple._1) { db => db.dropDatabase }
      }
    }

    // clear the mongo instances
    MongoDB.close
  }
}

