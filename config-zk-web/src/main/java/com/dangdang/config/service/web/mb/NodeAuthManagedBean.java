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
package com.dangdang.config.service.web.mb;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.dangdang.config.service.IAuthService;
import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.IRootNodeRecorder;
import com.dangdang.config.service.observer.IObserver;
import com.dangdang.config.service.observer.ISubject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * 节点授权
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@ManagedBean(name = "nodeAuthMB")
@SessionScoped
public class NodeAuthManagedBean implements Serializable, ISubject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nodeName;

	private String password;

	private String authedNode;

	@ManagedProperty(value = "#{authService}")
	private IAuthService authService;

	@ManagedProperty(value = "#{nodeService}")
	private INodeService nodeService;

	public INodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(INodeService nodeService) {
		this.nodeService = nodeService;
	}

	@ManagedProperty(value = "#{rootNodeRecorder}")
	private IRootNodeRecorder rootNodeRecorder;

	public void checkAuth() {
		boolean login = authService.auth(nodeName, password);
		FacesContext context = FacesContext.getCurrentInstance();
		if (login) {
			context.addMessage(null, new FacesMessage("登陆成功."));
			authedNode = nodeName;
			notify(authedNode, null);
			rootNodeRecorder.saveNode(authedNode);
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "登录失败.", "身份认证未通过."));
		}
	}

	public List<String> complete(String prefix) {
		List<String> tips = Lists.newArrayList();
		List<String> nodes = rootNodeRecorder.listNode();
		if (nodes != null) {
			for (String node : nodes) {
				if (node.startsWith(prefix)) {
					tips.add(node);
				}
			}
		}
		return tips;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthService(IAuthService authService) {
		this.authService = authService;
	}

	public final void setRootNodeRecorder(IRootNodeRecorder rootNodeRecorder) {
		this.rootNodeRecorder = rootNodeRecorder;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getPassword() {
		return password;
	}

	public String getAuthedNode() {
		return authedNode;
	}

	public void setAuthedNode(String authedNode) {
		this.authedNode = authedNode;
	}

	/**
	 * 观察者列表
	 */
	private final List<IObserver> watchers = Lists.newArrayList();

	@Override
	public void register(final IObserver watcher) {
		watchers.add(Preconditions.checkNotNull(watcher));
	}

	@Override
	public void notify(final String key, final String value) {
		for (final IObserver watcher : watchers) {
			watcher.notified(key, value);
		}
	}

	public void deleteNode() {
		boolean login = authService.auth(nodeName, password);
		FacesContext context = FacesContext.getCurrentInstance();
		authedNode = nodeName;
		notify(authedNode, null);
		if (login) {
			if (!nodeName.equals("/")) {
				nodeService.deleteNode(nodeName);
				rootNodeRecorder.removeNode(nodeName);
				context.addMessage(null, new FacesMessage("删除节点：'" + nodeName + "'成功！"));
				this.authedNode = null;
				this.nodeName = null;
			}
			else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "节点删除失败.", "根节点无法删除."));
			}
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "身份认证失败.", "身份认证未通过，无法删除此节点."));
		}
	}

}
