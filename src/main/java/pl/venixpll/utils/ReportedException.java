/*
 * LightProxy
 * Copyright (C) 2021.  VenixPLL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package pl.venixpll.utils;

public class ReportedException extends RuntimeException {
    private static final String __OBFID = "CL_00001579";
    /**
     * Instance of CrashReport.
     */
    private final Exception theReportedExceptionCrashReport;

    public ReportedException(Exception p_i1356_1_) {
        this.theReportedExceptionCrashReport = p_i1356_1_;
    }

    /**
     * Gets the CrashReport wrapped by this exception.
     */
   /*public CrashReport getCrashReport()
    {
        return this.theReportedExceptionCrashReport;
    }*/
    public Throwable getCause() {
        return this.theReportedExceptionCrashReport.getCause();
    }

    public String getMessage() {
        return this.theReportedExceptionCrashReport.getMessage();
    }
}
