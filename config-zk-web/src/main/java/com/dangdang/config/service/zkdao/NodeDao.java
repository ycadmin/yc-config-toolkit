/**
 * Copyright 1999-2014 dangdang.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dangdang.config.service.zkdao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.entity.PropertyItem;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
public class NodeDao extends BaseDao implements INodeDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeDao.class);

	@Override
	public List<PropertyItem> findProperties(String node) {
		LOGGER.debug("Find properties in node: [{}]", node);
		List<PropertyItem> properties = Lists.newArrayList();
		try {
			Stat stat = getClient().checkExists().forPath(node);
			if (stat != null) {
				GetChildrenBuilder childrenBuilder = getClient().getChildren();
				List<String> children = childrenBuilder.forPath(node);
				GetDataBuilder dataBuilder = getClient().getData();
				if (children != null) {
					for (String child : children) {
						String propPath = ZKPaths.makePath(node, child);
						PropertyItem item = new PropertyItem(child, new String(
								dataBuilder.forPath(propPath), Charsets.UTF_8));
						Stat childstat = getClient().checkExists().forPath(propPath);
						SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						item.setCrateTimeString(format.format(childstat.getCtime()));
						item.setModifyTimeString(format.format(childstat.getMtime()));
						item.setVersion(childstat.getVersion()+"");
						properties.add(item);
					}
				}
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return properties;
	}

	@Override
	public List<String> listChildren(String node) {
		LOGGER.debug("Find children of node: [{}]", node);
		List<String> children = null;
		try {
			Stat stat = getClient().checkExists().forPath(node);
			if (stat != null) {
				GetChildrenBuilder childrenBuilder = getClient().getChildren();
				children = childrenBuilder.forPath(node);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return children;
	}

	@Override
	public void deleteNode(String node) {
		LOGGER.info("delete node:", node);
		try {
			if (getClient().checkExists().forPath(node) != null) {
				getClient().delete().deletingChildrenIfNeeded().forPath(node);
				LOGGER.info("delete node:[{}] success, deleted at: "
						+ new Date(), node);
			}
			else
			{
				LOGGER.info("delete node error, node isn't exist.");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
