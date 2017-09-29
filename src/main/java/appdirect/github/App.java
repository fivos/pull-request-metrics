package appdirect.github;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.PullRequestService;

public class App {
	private static final String ACCESS_TOKEN = "";
	private static final String OWNER = "";
	private static final String REPO = "";

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Running ...");

		PullRequestService service = new PullRequestService();
		service.getClient().setOAuth2Token(ACCESS_TOKEN);

		RepositoryId repository = new RepositoryId(OWNER, REPO);

		PageIterator<PullRequest> pullRequestPages = service.pagePullRequests(repository, "closed", 100);
		PrintWriter writer = new PrintWriter("merged_prs.txt");
		int i = 0;
		while (pullRequestPages.hasNext()) {
			System.out.println(String.format("Processing page: %s", i++));
			Collection<PullRequest> prs = pullRequestPages.next();
			for (PullRequest pr : prs) {
				if (pr.getMergedAt() == null) {
					continue;
				}

				Date createdAt = pr.getCreatedAt();
				Date mergedAt = pr.getMergedAt();

				long mergeDiff = mergedAt.getTime() - createdAt.getTime();

 				// System.out.println(String.format("PR %d: title=%s,createdAt=%s,mergedAt=%s,mergeDiff=%d", i++, pr.getTitle(), createdAt, mergedAt, mergeDiff));
//				System.out.println(String.format("%s,%s,%d,%d,%d", createdAt, mergedAt, createdAt.getTime(), mergedAt.getTime(),mergeDiff));
				writer.println(String.format("%s,%s,%d,%d,%d", createdAt, mergedAt, createdAt.getTime(), mergedAt.getTime(),mergeDiff));
			}
		}

		writer.close();
	}
}
