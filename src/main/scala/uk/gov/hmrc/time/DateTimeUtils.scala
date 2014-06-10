/*
 * Copyright 2014 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc.time

import org.joda.time._

trait DateTimeUtils {
  def now = DateTime.now.withZone(DateTimeZone.UTC)

  def daysBetween(start: LocalDate, end: LocalDate): Int =
    Days.daysBetween(start.toDateTimeAtStartOfDay(DateTimeZone.UTC), end.toDateTimeAtStartOfDay(DateTimeZone.UTC)).getDays

  def isEqualOrAfter(date:LocalDate, laterDate:LocalDate):Boolean = date.isEqual(laterDate) || date.isBefore(laterDate)
}

object DateTimeUtils extends DateTimeUtils

private[time] trait TaxYearResolver {

  private[time] val now: () => DateTime

  private val ukTime = DateTimeZone.forID("Europe/London")

  def taxYearFor(dateToResolve: LocalDate): Int = {
    val year = dateToResolve.year.get

    if (dateToResolve.isBefore(new LocalDate(year, 4, 6)))
      year - 1
    else
      year
  }

  def currentTaxYear: Int = taxYearFor(new LocalDate(now(), ukTime))

  def currentTaxYearYearsRange = currentTaxYear to currentTaxYear + 1

  def startOfTaxYear(year: Int) = new LocalDate(year, 4, 6)

  def startOfCurrentTaxYear = startOfTaxYear(currentTaxYear)

  def endOfCurrentTaxYear = new LocalDate(currentTaxYear + 1, 4, 5)

  def startOfNextTaxYear = new LocalDate(currentTaxYear + 1, 4, 6)

  def taxYearInterval = new Interval(TaxYearResolver.startOfCurrentTaxYear.toDateTimeAtStartOfDay(ukTime),
    TaxYearResolver.startOfNextTaxYear.toDateTimeAtStartOfDay(ukTime))
}

object TaxYearResolver extends TaxYearResolver {
  override private[time] val now = () => DateTimeUtils.now
}