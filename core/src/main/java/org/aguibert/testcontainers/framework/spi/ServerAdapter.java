/*
 * Copyright (c) 2019 IBM Corporation and others
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package org.aguibert.testcontainers.framework.spi;

import java.io.File;

import org.testcontainers.images.builder.ImageFromDockerfile;

public interface ServerAdapter {

    public int getDefaultHttpPort();

    public int getDefaultHttpsPort();

    public int getDefaultAppStartTimeout();

    public ImageFromDockerfile getDefaultImage(File appFile);

}
