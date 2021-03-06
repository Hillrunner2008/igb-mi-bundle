/* 
 * Copyright 2015 Fondazione Istituto Italiano di Tecnologia.
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
package it.iit.genomics.cru.structures.bridges.userData;

import it.iit.genomics.cru.bridges.interactome3d.local.Interactome3DLocalRepository;
import java.util.HashMap;

/**
 *
 * @author Arnaud Ceol
 */
public class UserStructuresManager {

    private final HashMap<String, Interactome3DLocalRepository> indexes = new HashMap<>();

    private static UserStructuresManager instance = null;

    private UserStructuresManager() {
        super();
    }

    /**
     *
     * @return
     */
    public static UserStructuresManager getInstance() {
        if (instance == null) {
            instance = new UserStructuresManager();
        }

        return instance;
    }

    /**
     *
     * @param path
     * @return
     */
    public Interactome3DLocalRepository getUserRepository(String path) {
        if (false == indexes.containsKey(path)) {
            Interactome3DLocalRepository user = new Interactome3DLocalRepository(path);
            indexes.put(path, user);
        }

        return indexes.get(path);
    }

}
