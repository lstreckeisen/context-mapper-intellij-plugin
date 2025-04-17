package org.contextmapper.intellij.syntaxhighlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.features.semanticTokens.SemanticTokensColorsProvider
import org.eclipse.lsp4j.SemanticTokenModifiers
import org.eclipse.lsp4j.SemanticTokenTypes

class ContextMapperDSLSemanticTokensColorProvider : SemanticTokensColorsProvider {
    override fun getTextAttributesKey(
        tokenType: String,
        tokenModifiers: MutableList<String>,
        file: PsiFile
    ): TextAttributesKey? =
        when (tokenType) {
            SemanticTokenTypes.Comment ->
                TextAttributesKey.createTextAttributesKey(
                    "CML_COMMENT",
                    DefaultLanguageHighlighterColors.BLOCK_COMMENT,
                )
            SemanticTokenTypes.Keyword -> TextAttributesKey.createTextAttributesKey("CML_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
            SemanticTokenTypes.Type ->
                if (tokenModifiers.contains(SemanticTokenModifiers.Declaration)) {
                    TextAttributesKey.createTextAttributesKey("CML_TYPE_DECLARATION", DefaultLanguageHighlighterColors.CLASS_NAME)
                } else {
                    TextAttributesKey.createTextAttributesKey("CML_TYPE_DECLARATION", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
                }
            SemanticTokenTypes.String -> TextAttributesKey.createTextAttributesKey("CML_STRING", DefaultLanguageHighlighterColors.STRING)
            SemanticTokenTypes.Namespace ->
                TextAttributesKey.createTextAttributesKey(
                    "CML_NAMESPACE",
                    DefaultLanguageHighlighterColors.IDENTIFIER,
                )
            SemanticTokenTypes.Operator ->
                TextAttributesKey.createTextAttributesKey(
                    "CML_OPERATOR",
                    DefaultLanguageHighlighterColors.OPERATION_SIGN,
                )
            SemanticTokenTypes.EnumMember ->
                TextAttributesKey.createTextAttributesKey(
                    "CML_ENUM_MEMBER",
                    DefaultLanguageHighlighterColors.CONSTANT,
                )
            else -> null
        }
}
