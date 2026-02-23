package com.hazebyte.crate.cratereloaded.locale;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.*;

public class LocaleManager {

    private Locale defaultLocale;
    private final Map<Locale, LanguageTable> table = new HashMap<>();

    public LocaleManager(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void addMessages(@NotNull Locale locale, @NotNull Map<MessageKey, String> messages) {
        getTable(locale).addMessages(messages);
    }

    public String addMessage(@NotNull Locale locale, @NotNull MessageKey messageKey, String message) {
        return getTable(locale).addMessage(messageKey, message);
    }

    public boolean addMessageBundle(@NotNull String bundle, @NotNull Locale... locales) {
        for (Locale locale : locales) {
            return getTable(locale).addMessageBundle(bundle);
        }
        return false;
    }

    public boolean addMessageBundle(@NotNull String bundle, String dataFolderName, @NotNull Locale... locales) {
        for (Locale locale : locales) {
            return getTable(locale).addMessageBundle(bundle, dataFolderName);
        }
        return false;
    }

    public String getMessage(@NotNull MessageKey key) {
        Locale locale = getDefaultLocale();
        return getTable(locale).getMessage(key);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale locale) {
        this.defaultLocale = locale;
    }

    public @NotNull LanguageTable getTable(@NotNull Locale locale) {
        return table.computeIfAbsent(locale, LanguageTable::new);
    }
}
