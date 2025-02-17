/*
 * BreweryX Bukkit-Plugin for an alternate brewing process
 * Copyright (C) 2024 The Brewery Team
 *
 * This file is part of BreweryX.
 *
 * BreweryX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BreweryX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BreweryX. If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.dre.brewery.configuration.configurer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * List of the available translation<br />
 * Should we even use an enum here?<br />
 * <br />
 * The comments can be found under {@code langs/LANGUAGE.yml}
 */
public record Translation(String fileName) {

    // Languages added should have a config and a lang translation (resources/config-langs/, resources/languages/)

    public static final Translation EN = new Translation("en.yml");
    private static final List<Translation> DEFAULT_TRANSLATIONS = compileTranslations();

    public static Translation getTranslation(String language) {
        return new Translation(language.toLowerCase(Locale.ROOT) + ".yml");
    }

    public static List<Translation> getDefaultTranslations() {
        return DEFAULT_TRANSLATIONS;
    }

    private static List<Translation> compileTranslations() {
        Stream<String> languageFiles;
        try {
            URI uri = Translation.class.getResource("/resources").toURI();
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    myPath = fileSystem.getPath("/resources");
                    languageFiles = Files.walk(myPath, 1).map(Path::getFileName).map(Path::toString);
                }
            } else {
                myPath = Paths.get(uri);
                languageFiles = Files.walk(myPath, 1).map(Path::getFileName).map(Path::toString);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return languageFiles
            .filter(filename -> filename.endsWith(".yml"))
            .map(Translation::new)
            .toList();
    }
}
