/**
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.master;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableNotDisabledException;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.ipc.HRegionInterface;
import org.apache.hadoop.hbase.util.Writables;

import java.io.IOException;

abstract class ColumnOperation extends TableOperation {
  private final Log LOG = LogFactory.getLog(this.getClass());

  protected ColumnOperation(final HMaster master, final byte [] tableName)
  throws IOException {
    super(master, tableName);
  }

  @Override
  protected void processScanItem(String serverName, final HRegionInfo info)
  throws IOException {
    if (isEnabled(info)) {
      throw new TableNotDisabledException(tableName);
    }
  }

  protected void updateRegionInfo(HRegionInterface server, byte [] regionName,
    HRegionInfo i) throws IOException {
    Put put = new Put(i.getRegionName());
    put.add(HConstants.CATALOG_FAMILY, HConstants.REGIONINFO_QUALIFIER, Writables.getBytes(i));
    server.put(regionName, put);
    if (LOG.isDebugEnabled()) {
      LOG.debug("updated columns in row: " + i.getRegionNameAsString());
    }
  }
}
