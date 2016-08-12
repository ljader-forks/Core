/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.unifiedviews.commons.dao.impl;

import org.springframework.stereotype.Component;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import eu.unifiedviews.commons.dao.DBExecutionView;
import eu.unifiedviews.commons.dao.view.ExecutionView;

/**
 * Implementation of {@link DBPipelineView}.
 *
 * @author Škoda Petr
 */
@Component
class DBExecutionViewImpl extends DbAccessBase<ExecutionView> implements DBExecutionView {

    public DBExecutionViewImpl() {
        super(ExecutionView.class);
    }

}