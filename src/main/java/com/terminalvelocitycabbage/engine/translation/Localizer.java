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

/**
 * A class of utilities for localizing game texts. To use the localizer first all language configs shall be registered
 * with the {@link Language} abbreviation in toml files (en-us.toml for example) on the filesystem. Next all of the
 * translation keys should be registered with #registerTranslatableText for each of the localizable texts, and finally
 * the #init method should be called to compile the Localizer. After that any of the registered keys can be localized
 * to the currently configured language with the various #localize methods in this class.
 */
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

    /**
     * Localizes the specified translation key to the current language
     * @param localizableTextKey The key for the localizable text being localized
     * @return A localized string defined by the provided key
     */
    public String localize(Identifier localizableTextKey) {
        return translations.get(localizableTextKey);
    }

    /**
     * Localizes the specified translation key to the current language and substitutes arguments as provided
     * @param localizableTextKey The key for the localizable text being localized
     * @param args arguments that the localized text need to have substituted in, in order. {0} will be replaced by the
     *             string value of the first argument provided {1} the second etc.
     * @return A localized string defined by the provided key with substituted arguments
     */
    public String localize(Identifier localizableTextKey, Object... args) {
        return MessageFormat.format(localize(localizableTextKey), args);
    }

    /**
     * Registers a translation key to this localizer to be compiled on init
     * @param namespace The namespace of the translation (usually the entrypoint ID)
     * @param localizableTextKey The key defined by the localization file for this text being registered
     * @return The identifier created by this translatable text registry
     */
    public Identifier registerTranslatableText(String namespace, String localizableTextKey) {
        Identifier identifier = new Identifier(namespace, localizableTextKey);
        translations.put(identifier, null);
        return identifier;
    }

    /**
     * Changes the current language to the specified language
     * @param newLanguage The language you wish to switch this localizer to
     */
    public void changeLanguage(Language newLanguage) {
        language = newLanguage;
        init();
    }

    private void clearTranslations() {
        Map<Identifier, String> translations1 = new HashMap<>();
        translations.forEach((identifier, s) -> translations1.put(identifier, null));
        translations = translations1;
    }

    /**
     * Initializes this localizer by creating a list of config objects from the config files and caching them then
     * clearing all currently translated texts and going through each of the registered translation keys and gathering
     * their actual values for the current language or fallback language
     */
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
