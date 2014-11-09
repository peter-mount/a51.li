/*
 * Copyright 2014 Peter T Mount.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package onl.area51.a51li.sql;

/**
 *
 * @author Peter T Mount
 */
public class VisitCount
{

    private int total;
    private int lastMonth;
    private int lastWeek;

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public int getLastMonth()
    {
        return lastMonth;
    }

    public void setLastMonth( int lastMonth )
    {
        this.lastMonth = lastMonth;
    }

    public int getLastWeek()
    {
        return lastWeek;
    }

    public void setLastWeek( int lastWeek )
    {
        this.lastWeek = lastWeek;
    }

}
