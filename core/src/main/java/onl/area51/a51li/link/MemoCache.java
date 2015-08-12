/*
 * Copyright 2015 peter.
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
package onl.area51.a51li.link;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import onl.area51.a51li.memo.Memo;

/**
 *
 * @author peter
 */
@ApplicationScoped
@CacheDefaults(cacheName = "a51MemoCache")
public class MemoCache
        extends AbstractCache
{

    @CacheResult
    public Memo getMemo( @CacheKey long uid )
    {
        return get( Math.abs( uid ), "memo", Memo.fromSQL );
    }

}
