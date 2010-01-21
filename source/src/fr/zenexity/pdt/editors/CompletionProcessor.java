package fr.zenexity.pdt.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;


public class CompletionProcessor extends TemplateCompletionProcessor {

	protected Editor editor;
	private ISourceViewer sourceViewer;
	private String type;
	protected String contentType;

	public CompletionProcessor(String contentType, ISourceViewer sourceViewer, Editor editor) {
		this.contentType = contentType;
		this.editor = editor;
		this.type = editor.getClass().getName();
		this.sourceViewer = sourceViewer;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		return new ContextType(type);
	}

	@Override
	protected Image getImage(Template template) {
		return null;
	}

	@Override
	protected Template[] getTemplates(String contextTypeId) {
		return editor.getTemplates(contentType, getCtx());
	}

	protected String getCtx() {
		String txt = editor.documentProvider.document.get();
		String ctx = "";
		int position = sourceViewer.getSelectedRange().x - 1;
		while (position >= 0) {
			char c = txt.charAt(position--);
			if (c == ' ' || c == '\n') {
				break;
			}
			ctx = c + ctx;
		}
		return ctx;
	}

	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region, int relevance) {
		return new AutoCorrectIndentationTemplateProposal(template, context, region, getImage(template), relevance);
	}

}
