package org.bboss.elasticsearchtest.cluster;
/**

 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ClusterSetting;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-update-settings.html
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html
 *
 * </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/3/22 11:21
  * @version 1.0
 */
public class TestClusterSetting {
	@Test
	public void testGetClusterSetting(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
		System.out.println(clientInterface.getClusterSettings());

	}
	@Test
	public void updateClusterSetting(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
		ClusterSetting clusterSetting = new ClusterSetting();
		clusterSetting.setKey("indices.recovery.max_bytes_per_sec");
		clusterSetting.setValue("50mb");
		clusterSetting.setPersistent(true);
		clientInterface.updateClusterSetting(clusterSetting);
		System.out.println(clientInterface.getClusterSettings());
		clusterSetting = new ClusterSetting();
		clusterSetting.setKey("indices.recovery.max_bytes_per_sec");
		clusterSetting.setValue(null);
		clusterSetting.setPersistent(true);
		clientInterface.updateClusterSetting(clusterSetting);
		System.out.println(clientInterface.getClusterSettings());
	}

	@Test
	public void updateClusterSettings(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
		List<ClusterSetting> clusterSettingList = new ArrayList<ClusterSetting>();

		ClusterSetting clusterSetting = new ClusterSetting();
		clusterSetting.setKey("indices.recovery.max_bytes_per_sec");
		clusterSetting.setValue("50mb");
		clusterSetting.setPersistent(true);
		clusterSettingList.add(clusterSetting);

		clusterSetting = new ClusterSetting();
		clusterSetting.setKey("xpack.monitoring.collection.enabled");
		clusterSetting.setValue("true");
		clusterSetting.setPersistent(true);
		clusterSettingList.add(clusterSetting);

		clusterSetting = new ClusterSetting();
		clusterSetting.setKey("xpack.monitoring.collection.enabled");
		clusterSetting.setValue("true");
		clusterSetting.setPersistent(false);
		clusterSettingList.add(clusterSetting);

		clusterSetting = new ClusterSetting();
		clusterSetting.setKey("indices.recovery.max_bytes_per_sec");
		clusterSetting.setValue("50mb");
		clusterSetting.setPersistent(false);
		clusterSettingList.add(clusterSetting);

		clientInterface.updateClusterSettings(clusterSettingList);
		System.out.println(clientInterface.getClusterSettings(false));
		clusterSettingList = new ArrayList<ClusterSetting>();

		clusterSetting = new ClusterSetting();
		clusterSetting.setKey("xpack.monitoring.collection.enabled");
		clusterSetting.setValue(null);
		clusterSetting.setPersistent(true);
		clusterSettingList.add(clusterSetting);

		clusterSetting = new ClusterSetting();
		clusterSetting.setKey("indices.recovery.max_bytes_per_sec");
		clusterSetting.setValue(null);
		clusterSetting.setPersistent(false);
		clusterSettingList.add(clusterSetting);

		clientInterface.updateClusterSettings(clusterSettingList);
		System.out.println(clientInterface.getClusterSettings(false));
	}
	@Test
	public void updateUnassigned(){
		ClientInterface clientInterface =  ElasticSearchHelper.getRestClientUtil();
		clientInterface.unassignedNodeLeftDelayedTimeout("2d"); //????????????
		clientInterface.unassignedNodeLeftDelayedTimeout("demo","3d");//????????????demo??????
		System.out.println(clientInterface.executeHttp("demo/_settings?pretty",ClientInterface.HTTP_GET));//????????????demo??????
		clientInterface.unassignedNodeLeftDelayedTimeout("demo","3d");//????????????
		System.out.println(clientInterface.getIndiceSetting("demo","pretty"));//????????????demo??????
	}

	@Test
	public void updateNumberOfReplicas(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();

		clientInterface.updateNumberOfReplicas(1);//????????????
		clientInterface.updateNumberOfReplicas("demo",2);//????????????demo??????
		System.out.println(clientInterface.executeHttp("demo/_settings?pretty",ClientInterface.HTTP_GET));//????????????demo??????
		clientInterface.updateNumberOfReplicas("demo",3);//????????????demo??????
		System.out.println(clientInterface.getIndiceSetting("demo","pretty"));//????????????demo??????

	}

	@Test
	public void testSetting(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
		clientInterface.updateIndiceSetting("demo","index.unassigned.node_left.delayed_timeout","1d");
		System.out.println(clientInterface.getIndiceSetting("demo","pretty"));//????????????demo??????
		clientInterface.updateAllIndicesSetting("index.unassigned.node_left.delayed_timeout","2d");
		System.out.println(clientInterface.getIndiceSetting("demo","pretty"));//????????????demo??????
		Map<String,Object> settings = new HashMap<String,Object>();
		settings.put("index.unassigned.node_left.delayed_timeout","5d");
		settings.put("index.number_of_replicas",5);
		clientInterface.updateAllIndicesSettings(settings);
		System.out.println(clientInterface.getIndiceSetting("demo","pretty"));//????????????demo??????
		settings.put("index.unassigned.node_left.delayed_timeout","3d");
		settings.put("index.number_of_replicas",6);
		clientInterface.updateIndiceSettings("demo",settings);
		System.out.println(clientInterface.getIndiceSetting("demo","pretty"));//????????????demo??????

	}

	/**
	 * https://www.elastic.co/guide/en/elasticsearch/reference/6.3/rolling-upgrades.html
	 */
	@Test
	public void enableShared(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
//		System.out.println(clientInterface.flushSynced("demo"));//https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-synced-flush.html
		System.out.println(clientInterface.flushSynced());//https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-synced-flush.html
		System.out.println(clientInterface.disableClusterRoutingAllocation());//??????share allocation
		System.out.println(clientInterface.getClusterSettings(false));//?????????????????????????????????????????????????????????????????????
		System.out.println(clientInterface.enableClusterRoutingAllocation());//??????share allocation
		System.out.println(clientInterface.getClusterSettings(false));//?????????????????????????????????????????????????????????????????????

	}

	@Test
	public void disableShared(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
//		System.out.println(clientInterface.flushSynced("demo"));//https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-synced-flush.html
		System.out.println(clientInterface.flushSynced());//https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-synced-flush.html
		System.out.println(clientInterface.disableClusterRoutingAllocation());//??????share allocation

		System.out.println(clientInterface.getClusterSettings(false));//?????????????????????????????????????????????????????????????????????

	}

	@Test
	public void getClusterInfo(){
		ClientInterface clientInterface = ElasticSearchHelper.getRestClientUtil();
		//??????Elasticsearch????????????
		System.out.println(clientInterface.getClusterInfo());
		//??????Elasticsearch???????????????
		System.out.println(clientInterface.getElasticsearchVersion());


	}
}
