package org.playframework.playclipse.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class AutoCorrectIndentationTemplateProposal extends TemplateProposal {

	public AutoCorrectIndentationTemplateProposal(Template template, TemplateContext context, IRegion region, Image image) {
		super(template, context, region, image, 0);
	}

	public AutoCorrectIndentationTemplateProposal(Template template, TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
	}

	@SuppressWarnings("deprecation")
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		try {
			StringBuffer indentationPrefix = new StringBuffer();
			int o = getReplaceOffset() - 1;
			int insertionStart = getReplaceOffset();
			int lineStart = 0;
			for (; o > 0 && viewer.getDocument().getChar(o) != '\n'; o--)
				lineStart = o;

			for (int i = lineStart; i < insertionStart; i++) {
				char c = viewer.getDocument().getChar(i);
				if (c != ' ' && c != '\t')
					break;
				indentationPrefix.insert(0, c);
			}

			String pattern = getTemplate().getPattern();
			String originalPattern = pattern;
			pattern = pattern.replace("\n", (new StringBuilder(String.valueOf('\n'))).append(indentationPrefix.toString()).toString());
			getTemplate().setPattern(pattern);
			super.apply(viewer, trigger, stateMask, offset);
			getTemplate().setPattern(originalPattern);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}