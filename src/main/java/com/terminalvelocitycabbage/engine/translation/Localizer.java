package com.terminalvelocitycabbage.engine.translation;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceType;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Localizer {

    Language language;
    Map<Identifier, String> translations;
    Map<String, Map<Language, Config>> loadedConfigs;

    public Localizer() {
        this(Language.ENGLISH_UNITED_STATES);
    }

    public Localizer(Language language) {
        this.language = language;
        this.translations = new HashMap<>();
        this.loadedConfigs = new HashMap<>();
    }

    public String localize(Identifier localizableTextKey) {
        return translations.get(localizableTextKey);
    }

    public String localize(Identifier localizableTextKey, Object... args) {
        return MessageFormat.format(localize(localizableTextKey), args);
    }

    public Identifier registerTranslatableText(String namespace, String path) {
        Identifier identifier = new Identifier(namespace, path);
        translations.put(identifier, null);
        return identifier;
    }

    public void changeLanguage(Language newLanguage) {
        language = newLanguage;
        init();
    }

    private void clearTranslations() {
        Map<Identifier, String> translations1 = new HashMap<>();
        translations.forEach((identifier, s) -> translations1.put(identifier, null));
        translations = translations1;
    }

    public void init() {

        //Parse and cache configs from localization files if none have been loaded yet
        if (loadedConfigs.isEmpty()) {
            ConfigFormat<?> tomlFormat = TomlFormat.instance();
            ConfigParser<?> parser = tomlFormat.createParser();
            Map<String, Resource> localizationResources = ClientBase.getInstance().getFileSystem().getResourcesOfType(ResourceType.LOCALIZATION);
            for (Map.Entry<String, Resource> e : localizationResources.entrySet()) {
                Identifier resourceIdentifier = Identifier.of(e.getKey());
                Resource resource = e.getValue();
                String resourceString = resource.asString();
                String resourceName = resourceIdentifier.getName();
                loadedConfigs.putIfAbsent(resourceIdentifier.getNamespace(), new HashMap<>());
                loadedConfigs.get(resourceIdentifier.getNamespace()).put(
                        Language.fromAbbreviation(resourceName.substring(0, resourceName.length() - 5)), //Trim the .toml part of the name
                        parser.parse(resourceString)
                );
            }
        }

        //Load translations for selected language from cached configs
        clearTranslations();
        for (Identifier entry : translations.keySet()) {
            String translationNamespace = entry.getNamespace();
            String translationKey = entry.getName();
            Config config = loadedConfigs.get(translationNamespace).get(language);
            String value = config == null ? entry.toString() : config.get(translationKey);
            if (value == null) {
                List<String> fallbacks = config.get("meta.fallbacks");
                for (String fallback : fallbacks) {
                    Config fallbackConfig = loadedConfigs.get(translationNamespace).get(Language.fromAbbreviation(fallback));
                    if (fallbackConfig == null) {
                        Log.error("No fallback language found for language: " + fallback);
                        continue;
                    }
                    String fallbackValue = fallbackConfig.get(translationKey);
                    if (fallbackValue != null) {
                        value = fallbackValue;
                        break; //We don't want to look at the next fallback if this one has a value for this localized key
                    }
                }
            }
            if (value == null) value = entry.toString();
            translations.put(entry, value);
        }
    }
}
