/*
 * Copyright 2017 github.com/kaaz
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

package discordbot.db.model;

import discordbot.db.AbstractModel;

import java.sql.Timestamp;

public class OReplyPattern extends AbstractModel {
    public int userId = 0;
    public int id = 0;
    public int guildId = 0;
    public String tag = "";
    public String pattern = "";
    public String reply = "";
    public Timestamp createdOn = null;
    public long cooldown = 0;
}