
package net.flaxia.android.githubviewer.util;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.Repository;

public class RepositoryEx extends Repository {
    public RepositoryEx(GitHubAPI a) {
        super(a);
    }

    public Response search(final String query, final String language) {
        return HTTPGet("https://github.com/api/v2/json/repos/search/" + encode(query)
                + "?language=" + encode(language));
    }
}
