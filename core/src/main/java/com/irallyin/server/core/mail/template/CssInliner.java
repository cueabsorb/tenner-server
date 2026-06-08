package com.irallyin.server.core.mail.template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Very small CSS inliner: extracts rules from <style> tags and applies declarations
 * as inline style attributes to elements matching selectors. Supports basic selectors
 * that Jsoup understands (element, .class, #id, attribute selectors, descendant).
 *
 * NOTE: This is a simple inliner and does not implement full CSS specificity or
 * computed style logic. It's suitable for small email templates with simple rules.
 */
public final class CssInliner {

    private CssInliner() {}

    public static String inline(String html) {
        if (html == null) return null;
        Document doc = Jsoup.parse(html);

        Elements styleTags = doc.select("style");
        if (styleTags.isEmpty()) {
            return doc.body().html();
        }

        List<String> rules = new ArrayList<>();
        for (Element style : styleTags) {
            String css = style.data();
            // split by closing brace
            String[] parts = css.split("}");
            for (String p : parts) {
                String s = p.trim();
                if (s.isEmpty()) continue;
                rules.add(s + "}");
            }
            style.remove();
        }

        for (String rule : rules) {
            int idx = rule.indexOf('{');
            if (idx <= 0) continue;
            String selector = rule.substring(0, idx).trim();
            String decl = rule.substring(idx + 1, rule.length() - 1).trim();
            if (selector.isEmpty() || decl.isEmpty()) continue;

            try {
                Elements els = doc.select(selector);
                for (Element e : els) {
                    String existing = e.attr("style");
                    if (existing == null) existing = "";
                    if (!existing.endsWith(";") && !existing.isEmpty()) existing = existing + ";";
                    e.attr("style", existing + decl);
                }
            } catch (Exception ex) {
                // selector might be unsupported; ignore and continue
            }
        }

        // return inner HTML of body to avoid duplicating html/head
        return doc.body().html();
    }
}

