package org.playframework.playclipse.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.playframework.playclipse.EditorHelper;
import org.playframework.playclipse.Navigation;


public abstract class Editor extends TextEditor {
	
	ColorManager colorManager = new ColorManager();
	DocumentProvider documentProvider;
	Navigation nav;
	
	public Editor() {
		super();
		setSourceViewerConfiguration(new Configuration(this));
		documentProvider = new DocumentProvider(this);
		setDocumentProvider(documentProvider);
		for (String type : getTypes()) {
			type.intern();
		}
	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	// Helpers

	protected Navigation getNav() {
		if (nav == null) {
			nav = new Navigation(new EditorHelper(this));
		}
		return nav;
	}

	protected IPath getPath() {
		IFileEditorInput input = (IFileEditorInput)getEditorInput();
		return input.getFile().getFullPath();
	}

	protected IProject getProject() {
		IFile curfile = ((IFileEditorInput)getEditorInput()).getFile();
		IContainer container = curfile.getParent();
		while (container != null) {
			if (container instanceof IProject) {
				return (IProject)container;
			}
			container = container.getParent();
		}
		// Should not happen
		return null;
	}

	// Templates
	
	private List<Template> templates = new ArrayList<Template>();
	
	public Template[] getTemplates(String contentType, String ctx) {
		templates.clear();
		templates(contentType, ctx);
		List<Template> result = new ArrayList<Template>();
		for(Template t : templates) {
			if(t.getName().startsWith(ctx) || ctx.endsWith(t.getName())) {
				result.add(t);
			}
		}
		if(result.isEmpty() && ctx.equals("")) {
			result = templates;
		}
		return result.toArray(new Template[result.size()]);
	}
	
	public abstract void templates(String contentType, String ctx);
	
	public void template(String name, String description, String pattern) {
		templates.add(new Template(name, description, getClass().getName(), pattern, true));
	}
	
	// Auto-close
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ITextViewerExtension tve = (ITextViewerExtension)getSourceViewer();
		tve.appendVerifyKeyListener(new AutoCloser(this, (SourceViewer)getSourceViewer()));
	}
	
	public static String SKIP = "__skip";
	
	public abstract String autoClose(char pc, char c, char nc);
	
	// Hyperlinks
	
	public abstract IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region);

	public void openLink(IHyperlink link) {
		if (link.getTypeLabel().equals("action")) {
			String linkText = link.getHyperlinkText();
			if (linkText.startsWith("'") && linkText.endsWith("'")) {
				// Static file, e.g. @{'/public/images/favicon.png'}
				String path = linkText.substring(1, linkText.length() - 1);
				getNav().openOrCreate(path);
				return;
			}
			String nakedAction = linkText.replaceFirst("\\(.*\\)", "");
			if (nakedAction.indexOf('.') == -1) {
				// Relative reference, e.g. just "index"
				IFile curfile = ((IFileEditorInput)getEditorInput()).getFile();
				String controller = curfile.getParent().getName();
				nakedAction = controller + "." + nakedAction;
			}
			getNav().goToAction(nakedAction);
			return;
		}
		if (link.getTypeLabel().equals("tag")) {
			getNav().goToView("tags/" + link.getHyperlinkText().replace('.', '/') + ".html");
			return;
		}
		if (link.getTypeLabel().equals("extends") || link.getTypeLabel().equals("include")) {
			String path = link.getHyperlinkText();
			getNav().goToView(path);
		}
		if (link.getTypeLabel().equals("action_in_tag")) {
			System.out.println(link);
			// TODO
			return;
		}
	}
	
	protected BestMatch findBestMatch(final int position, Pattern... patterns) {
		Object[] line = getLine(position);
		int offset = (Integer)line[1];
		String text = (String)line[0];
		List<Matcher> matches = new ArrayList<Matcher>();
		for(Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()) {
				matches.add(matcher);
			}
		}
		List<BestMatch> bestMatches = new ArrayList<BestMatch>();
		for(Matcher matcher : matches) {
			if(matcher.start()+offset < position && matcher.end()+offset > position) {
				bestMatches.add(new BestMatch(matcher, offset));
			}
		}
		Collections.sort(bestMatches, new Comparator<BestMatch>() {
			@Override
			public int compare(BestMatch o1, BestMatch o2) {
				return (o1.matcher.start(1) - position) - (o2.matcher.start(1) - position);
			}
		});
		if(bestMatches.isEmpty()) {
			return null;
		}
		return bestMatches.get(0);
	}
	
	protected Object[] getLine(int offset) {
		String text = documentProvider.document.get();
		int start = offset, end = offset;
		while(start > 0 && text.charAt(start) != '\n') {
			start--;
		}
		while(end < text.length() && text.charAt(end) != '\n') {
			end++;
		}
		return new Object[] {text.substring(start > 0 ? start+1 : 0, end), start > 0 ? start+1 : 0};
	}
	
	public class BestMatch {
		
		public Matcher matcher;
		public int offset;
		
		public BestMatch(Matcher matcher, int offset) {
			this.matcher = matcher;
			this.offset = offset;
		}
		
		public boolean is(Pattern pattern) {
			return matcher.pattern().equals(pattern);
		}
		
		public String text() {
			return matcher.group(1);
		}
		
		public IHyperlink hyperlink(final String type, int startOffset, int endOffset) {
			final IRegion region= new Region(offset+matcher.start()+startOffset, matcher.end()-matcher.start()-startOffset+endOffset);
			return new IHyperlink() {

				@Override
				public IRegion getHyperlinkRegion() {
					return region;
				}

				@Override
				public String getHyperlinkText() {
					return matcher.group(1);
				}

				@Override
				public String getTypeLabel() {
					return type;
				}

				@Override
				public void open() {
					Editor.this.openLink(this);
				}
				
				@Override
				public String toString() {
					return getTypeLabel() + " --> " +getHyperlinkText();
				}
				
			};
		}
		
	}
	
	// Styles & types
	
	public TextAttribute style(RGB color) {
		return new TextAttribute(colorManager.getColor(color));
	}
	
	public TextAttribute style(RGB color, RGB back) {
		return new TextAttribute(colorManager.getColor(color), colorManager.getColor(back), 0);
	}
	
	public abstract String[] getTypes();
	public abstract TextAttribute getStyle(String type);
	
	// Scanner
	
	String content;
	int end,  begin,  end2,  begin2,  len;
	protected String state = "default";
	boolean eof = false;

	protected String found(String newState, int skip) {
		begin2 = begin;
		end2 = --end + skip;
		begin = end += skip;
		String lastState = state;
		state = newState;
		return lastState;
	}

	protected void reset() {
		eof = false;
		end = begin = end2 = begin2 = 0;
		state = "default";
		content = ((DocumentProvider)getDocumentProvider()).document.get();
		len = content.length();
	}

	protected boolean isNext(String s) {
		try {
			int i = end - 1;
			for(char c : s.toCharArray()) {
				if(c != content.charAt(i++)) {
					return false;
				}
				if(i > content.length()) {
					return false;
				}
			}
			return true;
		} catch(StringIndexOutOfBoundsException e) {
			return false;
		}
	}

	protected boolean nextIsSpace() {
		return isNext(" ") || isNext("\t");
	}

	TypedRegion nextToken() {
		for (;;) {

			int left = len - end;
			if (left == 0) {
				end++;
				found("default", 0);
				eof = true;
				return new TypedRegion(begin2, end2-begin2, "default");
			}

			end++;

			String token = scan();

			if(token != null) {
				return new TypedRegion(begin2, end2-begin2, token);
			}
		}
	}

	public abstract String scan();

}
