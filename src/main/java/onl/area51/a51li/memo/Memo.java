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
package onl.area51.a51li.memo;

import java.sql.ResultSet;
import java.sql.Timestamp;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public class Memo
{

    public static final SQLFunction<ResultSet, Memo> fromSQL = rs -> new Memo(
            rs.getLong( 1 ),
            rs.getString( 2 ),
            rs.getString( 3 ),
            MemoType.get( rs.getInt( 4 ) ),
            rs.getTimestamp( 5 )
    );

    public static final Memo REMOVED = new Memo();

    private final long id;
    private final String title;
    private final String text;
    private final Timestamp expires;
    private final MemoType memoType;
    private final boolean shareable;

    public Memo( long id, String title, String text, MemoType memoType, Timestamp expires )
    {
        this.id = id;
        this.title = title;
        this.text = text;
        this.expires = expires;
        this.memoType = memoType;
        shareable = true;
    }

    private Memo()
    {
        id = 0;
        title = "This memo has been removed";
        text = "The memo you have requested has been removed either:<ul><li>by the author,</li><li>the administrator</li><li>it had originally been marked for expiry at a specific date.</li></ul>";
        expires = null;
        memoType = MemoType.HTML;
        this.shareable = false;
    }

    public long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }

    public MemoType getMemoType()
    {
        return memoType;
    }

    public Timestamp getExpires()
    {
        return expires;
    }

    public boolean isShareable()
    {
        return shareable;
    }

}
